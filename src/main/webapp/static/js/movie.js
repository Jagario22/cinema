function toggleClass(elem, className) {
    if (elem.className.indexOf(className) !== -1) {
        elem.className = elem.className.replace(className, '');
    } else {
        elem.className = elem.className.replace(/\s+/g, ' ') + ' ' + className;
    }

    return elem;
}


function toggleMenuDisplay(e) {
    const dropdown = e.currentTarget.parentNode;
    const menu = dropdown.querySelector('.menu');
    const icon = dropdown.querySelector('.fa-angle-right');

    toggleClass(menu, 'hide');
}

function handleOptionSelected(e) {
    toggleClass(e.target.parentNode, 'hide');

    const id = e.target.id;
    const newValue = e.target.textContent + ' ';
    const titleElem = document.querySelector('.dropdown .title');

    titleElem.textContent = newValue;
    //trigger custom event
    document.querySelector('.dropdown .title').dispatchEvent(new Event('change'));
    //setTimeout is used so transition is properly shown
}

function handleTitleChange(e) {
    const result = document.getElementById('result');
    result.innerHTML = 'The result is: ' + e.target.textContent;
}

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');

    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1];
        }
    }
}

function fillDropDown(data) {
    data.forEach(function (item, index) {
        const idValue = index
        var dropdownMenu = document.getElementById('dropdown_menu')
        var option = document.createElement("div");
        option.className = "option";
        option.innerText = item.date.day;
        option.setAttribute("id", "option" + index
        )
        dropdownMenu.append(option)
    });


}


/*function fillMovieInfo(data) {
    const url = location.protocol + "//" + location.host + "/" + "cinema/";
    $("#film_image").attr("src", url + data.film.img);
    $(".movie-title").innerText = data.film.title
}*/

$(document).ready(function () {
    /*  $.ajax({
          url: '/cinema/movie',
          method: 'get',
          dataType: 'json',
          headers: {'Access-Control-Allow-Origin': 'http://The web site allowed to access'},
          data: {
              command: getUrlParameter("command"),
              id: getUrlParameter("id")
          },
          cache: false,
          success: function (data) {
              fillMovieInfo(data)
          },
          error: function (jqXHR, exception) {
              if (jqXHR.status === 0) {
                  alert('Not connect. Verify Network.');
              } else if (jqXHR.status === 404) {
                  alert('Requested page not found (404).');
              } else if (jqXHR.status === 500) {
                  alert('Internal Server Error (500).');
              } else if (exception === 'parsererror') {
                  alert('Requested JSON parse failed.');
              } else if (exception === 'timeout') {
                  alert('Time out error.');
              } else if (exception === 'abort') {
                  alert('Ajax request aborted.');
              } else {
                  alert('Uncaught Error. ' + jqXHR.responseText);
              }
          }
      });*/
    /*  const dropdownTitle = document.querySelector('.dropdown .title');
      const dropdownOptions = document.querySelectorAll('.dropdown .option');

      dropdownTitle.addEventListener('click', toggleMenuDisplay);
      dropdownOptions.forEach(option => option.addEventListener('click', handleOptionSelected));
      document.querySelector('.dropdown .title').addEventListener('change', handleTitleChange);*/

    $(document).on("click", ".dropdown .title", function () {  // When HTML DOM "click" event is invoked on element with ID "somebutton", execute the following function...
        $.ajax({
            url: '/cinema/movie',
            method: 'get',
            dataType: 'json',
            headers: {'Access-Control-Allow-Origin': 'http://The web site allowed to access'},
            data: {
                command: 'showSessions',
                id: getUrlParameter("id")
            },
            cache: false,
            success: function (data) {
                fillDropDown(data)
            },
            error: function (jqXHR, exception) {
                if (jqXHR.status === 0) {
                    alert('Not connect. Verify Network.');
                } else if (jqXHR.status === 404) {
                    alert('Requested page not found (404).');
                } else if (jqXHR.status === 500) {
                    alert('Internal Server Error (500).');
                } else if (exception === 'parsererror') {
                    alert('Requested JSON parse failed.');
                } else if (exception === 'timeout') {
                    alert('Time out error.');
                } else if (exception === 'abort') {
                    alert('Ajax request aborted.');
                } else {
                    alert('Uncaught Error. ' + jqXHR.responseText);
                }
            }
        });
    });
});