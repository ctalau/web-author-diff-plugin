
var left = document.getElementById('left');
var right = document.getElementById('right');

var query = getQueryParams(document.location.search);

var diffId = Math.random();
var leftSrc = '../../app/oxygen.html?url=' + (query.base + query.left) + '&side=left&diff_id=' + diffId;
var rightSrc = '../../app/oxygen.html?url=' + (query.base + query.right) + '&side=right&diff_id=' + diffId;

for (param in query) {
  if (param !== 'base' && param !== 'left' && param !== 'right') {
    leftSrc += '&' + param + '=' + query[param];
    rightSrc += '&' + param + '=' + query[param];
  }
}

left.src = leftSrc;
function loadRightEditor() {
  right.src = rightSrc;
}


function getQueryParams(qs) {
  qs = qs.split('+').join(' ');

  var params = {},
      tokens,
      re = /[?&]?([^=]+)=([^&]*)/g;

  while (tokens = re.exec(qs)) {
    params[tokens[1]] = tokens[2];
  }

  return params;
}



