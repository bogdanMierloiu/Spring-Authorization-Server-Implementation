function parseAccessTokenTimeDuration() {
    let durationString = document.getElementById('accessTokenTimeToLive').value;
    let durationWithoutSuffix = durationString.replace(/[^0-9]/g, '');
    let parsedDuration = parseInt(durationWithoutSuffix);
    if (durationString.endsWith('H')) {
        parsedDuration = parsedDuration * 60;
    }
    document.getElementById('accessTokenTimeToLive').value = parsedDuration;
}

function parseRefreshTokenTimeDuration() {
    let durationString = document.getElementById('refreshTokenTimeToLive').value;
    let durationWithoutSuffix = durationString.replace(/[^0-9]/g, '');
    let parsedDuration = parseInt(durationWithoutSuffix);
    if (durationString.endsWith('H')) {
        parsedDuration = parsedDuration * 60;
    }
    document.getElementById('refreshTokenTimeToLive').value = parsedDuration;
}