/**
 * Created by travi on 2/28/2018.
 */
$(function() {
    $('.js-nav a, .js-connect').click(function(e) {
        e.preventDefault();
        $('body, html').animate({
            scrollTop: $($.attr(this, 'href')).offset().top
        }, 750);
    });
});