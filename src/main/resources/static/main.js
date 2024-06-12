const BASIC_DOMAIN = "http://localhost:8080";

const VOTE_RESULT_KEY = "voteResult";
const IMAGES_IDS_KEY = "imagesIds";
const CURRENT_INDEX_KEY = "currentIndex";

function getImagesIdsListFromStorage() {
    return JSON.parse(localStorage.getItem(IMAGES_IDS_KEY));
}

function getCurrentIndexFromStorage() {
    return parseInt(localStorage.getItem(CURRENT_INDEX_KEY));
}

function updateCurrentIndexInStorage(value) {
    localStorage.removeItem(CURRENT_INDEX_KEY);
    localStorage.setItem(CURRENT_INDEX_KEY, JSON.stringify(value));
}


window.onload = async function () {
    const imagesIds = await getRandomImagesIdList();
    const currentIndex = 0;
    localStorage.setItem(IMAGES_IDS_KEY, JSON.stringify(imagesIds));
    localStorage.setItem(CURRENT_INDEX_KEY, JSON.stringify(currentIndex));
    localStorage.setItem(VOTE_RESULT_KEY, JSON.stringify([]));

    await loadImageWithVotes(imagesIds[currentIndex]);
}

async function loadImageWithVotes(imageId) {
    const imageElement = $("#image");
    const yesVoteButtonElement = $("#yes");
    const noVoteButtonElement = $("#no");

    imageElement.attr("src", await fetchImage(imageId));

    yesVoteButtonElement.off();
    noVoteButtonElement.off();

    yesVoteButtonElement.on('click', async () => await vote(imageId, 1));
    noVoteButtonElement.on('click', async () => await vote(imageId, 0));
}

async function getRandomImagesIdList() {
    const response = await fetch("http://localhost:8080/images/random?counter=20");
    return response.json();
}

async function fetchImage(imageId) {
    const response = await fetch(`http://localhost:8080/images/${imageId}/download`);
    const imageData = await response.blob();
    return URL.createObjectURL(imageData);
}

async function vote(imageId, value) {
    const result = {
        "imageId": imageId,
        "vote": value
    };
    const voteResults = JSON.parse(localStorage.getItem(VOTE_RESULT_KEY));
    let currentIndex = getCurrentIndexFromStorage();
    let imagesIds = getImagesIdsListFromStorage();

    voteResults.push(result);
    localStorage.setItem(VOTE_RESULT_KEY, JSON.stringify(voteResults));

    if (currentIndex < imagesIds.length) {
        await loadImageWithVotes(imagesIds[currentIndex]);
        updateCurrentIndexInStorage(currentIndex + 1);
    } else {
        await sendVotesResult();
        window.location.href = 'main.html';
    }
}

async function sendVotesResult() {
    const votesResult = JSON.parse(localStorage.getItem(VOTE_RESULT_KEY));
    await fetch('http://localhost:8080/images/vote', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(votesResult)
    });
}

