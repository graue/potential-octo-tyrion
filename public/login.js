// Crudely copied from Pots, fyi's login.js.

function ajaxPost(url, data, success, error) {
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: success,
        error: error,
        dataType: 'json'
    });
}

var gotAssertion = function(assertion) {
    if (!assertion)
        return;

    ajaxPost('/token', 'assertion=' + encodeURIComponent(assertion),
                function(data, txtStatus, xhr) {
                    console.log('successfully got a token!');
                    console.log('server says my email is: ' +
                                data.email);
                    console.log('and the token is: ' + data.token);
                },
                function(xhr, txtStatus) {
                    console.log('error! text status: ' + txtStatus);
                    console.log('status code: ' + xhr.status);
                });
}

var logoutCallback = function() {
    alert('not implemented');
    return; // FIXME

    ajaxPost('/api/logout', '',
                function() { location.reload(true); },
                function(xhr) { alert('Logout error: code '
                                    + xhr.status); });
}

var attachAuthHandlers = function() {
    $('#signin').on('click', function(event) {
        event.preventDefault();
        navigator.id.get(gotAssertion);
    });

    $('#signout').on('click', function(event) {
        event.preventDefault();
        navigator.id.logout(logoutCallback);
    });
};

$(function() { attachAuthHandlers(); });
