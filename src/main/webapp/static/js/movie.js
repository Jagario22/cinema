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
    const newValue = e.target.textContent;
    const titleElem = document.querySelector('.dropdown .title');

    titleElem.textContent = newValue;
    titleElem.id = id
    document.querySelector('.dropdown .title').dispatchEvent(new Event('change'));
}

function handleTitleChange(e) {
    if (sessions.length === 0) {
        return;
    }
    const result = document.getElementById('result');
    $(".time").remove();
    let sessionId = document.querySelector('.dropdown .title').id.match(/\d+/g);
    const timeArr = sessions[sessionId - 1].timeList;
    let timeList = document.createElement("ul");
    timeList.className = "time";
    timeArr.forEach(function (timeElement, index) {
        let timeLi= document.createElement("li");
        const timeLink = document.createElement("a");
        const hour = timeElement.hour;
        let minute = timeElement.minute;
        if (minute < 10) {
            minute = "0" + minute;
        }
        timeLink.textContent = hour + ":" + minute;
        timeLink.id = "time" + (index + 1);
        timeLink.href = getTicketUrl(sessionId);
        timeLi.append(timeLink);
        timeList.append(timeLi);
    });
    result.append(timeList);
}

function getTicketUrl(sessionId) {
    return getUrl() + "?filmId=" + getUrlParameter("id") + "&sessionId=" + sessionId;
}

function getUrlParameter(sParam) {
    const url = new URL(document.URL);
    return url.searchParams.get(sParam);
}


function getUrl() {
    return location.protocol + '//' + location.host + location.pathname;
}

function fillDropDown(data) {
    const url = new URL(document.URL);
    let lang = url.searchParams.get("lang");
    if (lang === null)
        lang = "ru"
    const options = {weekday: 'long', month: 'long', day: 'numeric'};

    $(".option").remove();
    sessions = [];
    data.forEach(function (item, index) {
        sessions.push(item);
        const dropdownMenu = document.getElementById('dropdown_menu');
        const option = document.createElement("div");
        option.className = "option";
        const sessionDate =new Date(item.date.year, item.date.month, item.date.day)
            .toLocaleDateString(lang, options);
        const titleElem = document.querySelector('.dropdown .title').textContent;
        if (titleElem !== sessionDate) {
            option.innerText = sessionDate
            option.setAttribute("id", "option" + (index + 1));
            option.addEventListener('click', handleOptionSelected)
            dropdownMenu.append(option);
        }
    });


}

let sessions = []

$(document).ready(function () {
    document.querySelector('.dropdown .title').addEventListener('change', handleTitleChange);
    $(document).on("click", ".dropdown .title", function (e) {
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
                toggleMenuDisplay(e);
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