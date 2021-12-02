function toggleClass(elem, className) {
    if (elem.className.indexOf(className) !== -1) {
        elem.className = elem.className.replace(className, '');
    } else {
        elem.className = elem.className.replace(/\s+/g, ' ') + className;
    }

    return elem;
}

function toggleMenuDisplay(e) {
    const dropdown = e.currentTarget.parentNode;
    const menu = dropdown.querySelector('.menu');
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

function setEmptySessionsTitle(lang) {
    const titleElem = document.querySelector('.dropdown .title');
    if (lang === "ru") {
        titleElem.textContent = '\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u0441\u0435\u0430\u043d\u0441\u044b \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u044b'
    } else if (lang === "en") {
        titleElem.textContent = "No active movie sessions found"
    }
}

function getPlacesTitle(lang) {
    let placesTitle;
    if (lang === "ru") {
        placesTitle = "\u043a\u043e\u043b\u002d\u0432\u043e \u043c\u0435\u0441\u0442";
    } else if (lang === "en") {
        placesTitle = "places count"
    }
    return placesTitle;
}

function getTime(sessionTimeInfo) {
    const hour = sessionTimeInfo.hour;
    let time = sessionTimeInfo.minute;
    if (time < 10) {
        time = "0" + time;
    }

    return hour + ":" + time;
}


function handleTitleChange(e) {
    if (sessions.length === 0) {
        setEmptySessionsTitle(getLang());
        return;
    }
    const placesTitle = getPlacesTitle(getLang());
    const result = document.getElementById('result');
    $(".time").remove();

    let sessionIndex = document.querySelector('.dropdown .title').id.match(/\d+/g);
    const session = sessions[sessionIndex - 1]
    const sessionsInfo = session.sessionsInfo;
    let timeList = document.createElement("ul");
    timeList.className = "time";
    sessionsInfo.forEach(function (sessionInfo, index) {
        let timeLi = document.createElement("li");
        const timeLink = document.createElement("a");
        const time = getTime(sessionInfo.time)

        timeLink.textContent = time + ", " + placesTitle + ": " + sessionInfo.freePlacesCount;
        timeLink.id = "time" + (index + 1);
        timeLink.href = getTicketUrl(sessionInfo.id);
        timeLi.append(timeLink);
        timeList.append(timeLi);
    });
    result.append(timeList);
}

function getTicketUrl(sessionId) {
    return getUrl() + "?command=showSession&filmId=" + getUrlParameter("id") + "&sessionId=" + sessionId;
}

function getUrlParameter(sParam) {
    const url = new URL(document.URL);
    return url.searchParams.get(sParam);
}

function getLang() {
    let lang = getUrlParameter("lang");
    if (lang === null)
        lang = "ru"
    return lang;
}

function getUrl() {
    return location.protocol + '//' + location.host + location.pathname;
}

function fillDropDown(data) {
    if (data.length === 0) {
        document.querySelector('.dropdown .title').dispatchEvent(new Event('change'));
        return;
    }
    clearDropDown();

    data.forEach(function (sessionInfo, index) {
        sessions.push(sessionInfo);
        const dropdownMenu = document.getElementById('dropdown_menu');
        const sessionDate = getDate(sessionInfo, getLang(), {weekday: 'long', month: 'long', day: 'numeric'});
        const dropDownTitle = document.querySelector('.dropdown .title').textContent;
        if (dropDownTitle !== sessionDate) {
            const option = createOption(sessionDate, index, handleOptionSelected)
            dropdownMenu.append(option);
        }
    });
}

function getDate(item, lang, formatOptions) {
    return new Date(item.date.year, item.date.month - 1, item.date.day)
        .toLocaleDateString(lang, formatOptions);
}

function createOption(date, index, e) {
    let option = document.createElement("div");
    option.className = "option";
    option.innerText = date;
    option.setAttribute("id", "option" + (index + 1));
    option.addEventListener('click', e);
    return option;
}

function clearDropDown() {
    $(".option").remove();
    sessions = [];
}

function filmSessionsListSet(dropDownClassName) {
    if (document.querySelector(dropDownClassName) != null) {
        document.querySelector(dropDownClassName).addEventListener('change', handleTitleChange);
        $(document).on("click", dropDownClassName, function (e) {
            if (document.getElementById("dropdown_menu").className === "menu pointerCursor hide") {
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
            }
            toggleMenuDisplay(e);
        });
    }
}

function handleTicketInfoChanged(e) {
    let selectedRow = $('#rowSelect option:selected')[0].value;
    let selectedPlace = $('#placeSelect option:selected')[0].value;


    if (selectedPlace !== '') {
        document.getElementById("selected_row_info").textContent = selectedRow;
        document.getElementById("selected_place_info").textContent = selectedPlace;
        document.getElementById("ticket_type_info").textContent = selectedTicket.ticketType.name;
        document.getElementById("price").textContent = selectedTicket.ticketType.price;
    }
}

function clearTicketInfo() {
    if (document.getElementById("selected_row_info").textContent !== "") {
        document.getElementById("selected_row_info").textContent = "";
        document.getElementById("selected_place_info").textContent = "";
        document.getElementById("ticket_type_info").textContent = "";
        document.getElementById("price").textContent = "";
    }
}

function checkPlace(selectedRow, placeNumber, countOfRowPlaces) {
    let startLimit = selectedRow * countOfRowPlaces - countOfRowPlaces + 1;
    let endLimit = selectedRow * countOfRowPlaces;

    return placeNumber >= startLimit && placeNumber <= endLimit;
}


function extractPlace(selectedRow, placeNumber, countOrRowPlaces) {
    let result = placeNumber;
    while (result > countOrRowPlaces) {
        result -= countOrRowPlaces;
    }

    return result;
}

function fillPlaceSelect(data, selectedRow) {
    clearTicketInfo();
    if (data != null) {
        if (data.length === 0)
            return;

        let options = $('#placeSelect option');
        options.get(0).selected = 'selected'
        for (let i = 1; i < options.length; i++) {
            let o = options.get(i);
            o.disabled = true;
            o.className = "text-danger";
        }

        const countOfPlaces = selectedRow * options.length;
        data = data.filter(d => checkPlace(selectedRow, d.placeNumber, options.length - 1));

        for (let i = 0; i < data.length; i++) {
            let query = "\"" + extractPlace(selectedRow, data[i].placeNumber, options.length - 1) + "\"";
            selectedTicket = data[i];
            let option = $('#placeSelect option[value=' + query + ']').get(0);
            option.disabled = false;
            option.className = "";
        }
    }
}

function ticketListSet() {
    if (document.querySelector('#placeSelect') != null) {
        document.querySelector('#placeSelect').addEventListener('change', handleTicketInfoChanged);
        $(document).on("focus", '#placeSelect', function (e) {
            let selectedRow = $('#rowSelect option:selected').text();
            $.ajax({
                url: '/cinema/movie',
                method: 'get',
                dataType: 'json',
                headers: {'Access-Control-Allow-Origin': 'http://The web site allowed to access'},
                data: {
                    command: 'showTickets',
                    sessionId: getUrlParameter("sessionId")
                },
                cache: false,
                success: function (data) {
                    fillPlaceSelect(data, selectedRow)
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
        document.querySelector('#ticket').addEventListener('submit', function (e) {
            let placeSelect = $('#placeSelect option:selected').get(0);
            if (placeSelect.value !== '') {
                $('#rowSelect').get(0).disabled = true;
                placeSelect.value = selectedTicket.id;
            }
            return true;
        });
    }
}




let sessions = []
let selectedTicket = {}
$(document).ready(function () {
    filmSessionsListSet('.dropdown .title')
    ticketListSet();

});