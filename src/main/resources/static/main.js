const BASIC_DOMAIN = "http://localhost:8080";

const VOTE_RESULT_KEY = "voteResult";

imagesIds = [];
currentIndex = 0;

window.onload = async function () {
    imagesIds = await getRandomImagesIdList();
    localStorage.setItem(VOTE_RESULT_KEY, JSON.stringify([]));
    await loadImageWithVotes(imagesIds[currentIndex]);
}

async function loadImageWithVotes(imageId) {
    const imageElement = document.getElementById("image");
    const yesVoteButtonElement = document.getElementById("yes");
    const noVoteButtonElement = document.getElementById("no");

    imageElement.src = await fetchImage(imageId);

    yesVoteButtonElement.removeEventListener('click', async () => await vote(imageId, 1));
    noVoteButtonElement.removeEventListener('click', async () => await vote(imageId, 0));

    yesVoteButtonElement.addEventListener('click', async () => await vote(imageId, 1));
    noVoteButtonElement.addEventListener('click', async () => await vote(imageId, 0));
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

    voteResults.push(result);
    localStorage.setItem(VOTE_RESULT_KEY, JSON.stringify(voteResults));

    if (currentIndex < imagesIds.length) {
        await loadImageWithVotes(imagesIds[currentIndex]);
        currentIndex++;
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

