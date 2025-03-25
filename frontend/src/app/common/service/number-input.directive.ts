import { Directive, HostListener, ElementRef } from '@angular/core';

@Directive({
    selector: '[appNumberInput]'
})
export class NumberInputDirective {
    constructor(private el: ElementRef) {}

    @HostListener('input', ['$event'])
    onInput(event: Event) {
        const inputElement = this.el.nativeElement;
        const cursorPosition = inputElement.selectionStart;

        let input = inputElement.value;

        input = input.replace(',', '.');

        input = input.replace(/[^0-9.]/g, '');
        input = input.replace(/(\..*)\./g, '$1');

        inputElement.value = input;

        inputElement.setSelectionRange(cursorPosition, cursorPosition);
    }
}
