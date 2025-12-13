package pl.muybien.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.muybien.wallet.asset.AssetService;
import pl.muybien.wallet.asset.dto.AssetAggregateDTO;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WalletPublisher {

    private final WalletSubscriptionRegistry registry;
    private final AssetService assetService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRateString = "${wallet.websocket.refresh-interval-ms:5000}")
    public void publishWalletSnapshots() {
        for (Map.Entry<String, WalletSubscription> entry : registry.getSubscriptions().entrySet()) {
            WalletSubscription subscription = entry.getValue();

            List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(
                    subscription.customerId(),
                    subscription.currencyType()
            );

            String destination = "/topic/wallet/" + subscription.currencyType();
            messagingTemplate.convertAndSend(destination, assets);
        }
    }
}
