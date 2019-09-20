/**
 * Particleground demo
 * @author Jonathan Nicol - @mrjnicol
 */

$(document).ready(function() {
  $('#particles').particleground({
    dotColor: 'rgba(157,178,189,0.31)',
    lineColor: 'rgba(157,178,189,0.31)'
  });
  $('.intro').css({
    'margin-top': -($('.intro').height() / 2)
  });
});
