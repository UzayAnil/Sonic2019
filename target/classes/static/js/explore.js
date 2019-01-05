/**
 * Created by ruzieljonm on 10/09/2018.
 */
var cardLength = document.querySelectorAll('.card').length;
var cardHeader = document.querySelectorAll('.card_header');
console.log(cardLength, cardHeader);
for (var i = 0; i < cardLength; i++) {if (window.CP.shouldStopExecution(0)) break;
    var cardHeaderUrl = cardHeader[i].getAttribute('data-background');
    cardHeader[i].style.background = 'url(' + cardHeaderUrl + ')no-repeat center center / cover';
}window.CP.exitedLoop(0);