/*
Este codigo pone nofollow a todos los enlaces salientes
*/

jQuery(document).ready(function($){
  // add nofollow to http links
  $('a[href*="http://"]:not([href*="http://www.ufv.es"], [href*="http://ddfv.ufv.es"])').attr('rel', 'nofollow');

  // add nofollow to https links
  $('a[href*="https://"]:not([href*="http://www.ufv.es"])').attr('rel', 'nofollow');
});