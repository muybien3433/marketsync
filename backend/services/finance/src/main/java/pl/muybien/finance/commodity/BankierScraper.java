//package pl.muybien.finance.commodity;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import pl.muybien.finance.updater.FinanceUpdater;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BankierScraper extends FinanceUpdater implements CommandLineRunner {
//
//    private final String URL = "https://www.bankier.pl/surowce/notowania";
//
//    @Override
//    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(fixedRateString = "${bankier.update-rate-ms}")
//    public void scheduleUpdate() {
//        enqueueUpdate("bankier");
//    }
//
//    @Override
//    @Transactional
//    protected void updateAssets() {
//        log.info("Starting the update of Bankier data...");
//        try {
//            Document doc = Jsoup.connect(URL)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
//                    .get();
//
//            Element table = doc.select("table.sortTable").first();
//            Elements rows = table.select("tbody tr");
//
//            for (Element row : rows) {
//                Elements cols = row.select("td");
//
//                String name = cols.get(0).text();
//                String price = cols.get(1).text();
//                String change = cols.get(2).text();
//
//                System.out.println("Commodity: " + name);
//                System.out.println("Price: " + price);
//                System.out.println("Change: " + change);
//                System.out.println("---------------------");
//            }
//            log.info("Finished updating Bankier data");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//    }
//}