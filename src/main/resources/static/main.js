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


async function prepareAndLoadFirstImage() {
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

    updateCurrentIndexInStorage(getCurrentIndexFromStorage() + 1);
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
    } else {
        await sendVotesResult();
        window.location.href = 'display-images-list.html';
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

async function uploadImages() {
    const uploadFile = document.getElementById('uploadFile').files[0];
    const imagesCategory = document.getElementById('category').value;

    const formData = new FormData();
    formData.append('file', uploadFile)
    formData.append('category', imagesCategory);

    await fetch('http://localhost:8080/images/upload', {
        method: 'POST',
        body: formData
    }).then(res => {
        alert('Plik poprawnie wysłany');
    })
        .catch(error => alert(error))
}

async function loadListOfImages() {
    const imagesIdsList = getImagesIdsListFromStorage();
    let displayAllImagesContainerDiv = $("<div></div>").addClass("display-all-images-container");
    $('body').append(displayAllImagesContainerDiv);

    const response = await fetch('http://localhost:8080/images', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(imagesIdsList)
    });

    const imageInfoList = await response.json();

    for (let i = 0; i < imageInfoList.length; i++) {
        displayAllImagesContainerDiv.append(await createDivEntryInListView(imageInfoList[i]));
    }
}

async function createDivEntryInListView(imageInfo) {
    let imageDisplayContainerDiv = $("<div></div>").addClass("image-info-display-container");
    let imageDisplayDiv = $("<div></div>").addClass("image-display");
    let imageInfoDisplayDiv = $("<div></div>").addClass("image-info-display");

    let categoryDisplayDiv = $("<div></div>").addClass("category-display");
    let allVotesDisplayDiv = $("<div></div>").addClass("all-votes-display");
    let yesVotesDisplayDiv = $("<div></div>").addClass("yes-votes-display");
    let noVotesDisplayDiv = $("<div></div>").addClass("no-votes-display");

    categoryDisplayDiv.text(`Kategoria: ${imageInfo.category}`)
    allVotesDisplayDiv.text(`Wszystkich głosów: ${imageInfo.allVotesCounter}`);
    yesVotesDisplayDiv.text(`Głosów na tak: ${imageInfo.isArtVotesCounter}`);
    noVotesDisplayDiv.text(`Głosów na nie: ${imageInfo.isNotArtVotesCounter}`);

    const imageData = await fetchImage(imageInfo.imageId);
    const imgElement = $("<img alt=\"\">").attr("src", imageData);

    imageDisplayDiv.append(imgElement);

    imageInfoDisplayDiv.append(categoryDisplayDiv);
    imageInfoDisplayDiv.append(allVotesDisplayDiv);
    imageInfoDisplayDiv.append(yesVotesDisplayDiv);
    imageInfoDisplayDiv.append(noVotesDisplayDiv);

    imageDisplayContainerDiv.append(imageDisplayDiv);
    imageDisplayContainerDiv.append(imageInfoDisplayDiv);

    return imageDisplayContainerDiv;
}
