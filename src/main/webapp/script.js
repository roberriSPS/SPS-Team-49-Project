// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function bookDisplay() {
    const userText  = document.querySelector("#userText");
    //console.log(userText.value);
    const responseFromServer = await fetch(`/sentiment?message=${userText.value}`);
    //const text = await responseFromServer.text();
    //console.log(text);
    const books = await responseFromServer.json();
    const displayBook = document.getElementById('display-book');
    displayBook.innerHTML = '';

    //make a list of all book options
    const bookList = books.items;

    //generate a random number to examine, and verify that it is in english:
    const rand = await randNum(bookList.length);
    var match = 0;

    while (match == 0) {
    if (bookList[rand].volumeInfo.language === "en") {
      var title =  bookList[rand].volumeInfo.title;
      var author = bookList[rand].volumeInfo.authors[0];
      var description = bookList[rand].volumeInfo.description;
      var link = bookList[rand].volumeInfo.previewLink;
      match = 1;
    }
    else {
        rand = await randNum(bookList.length);
    }
    }   

    //check whether there are multiple authors:

    if ((bookList[rand].volumeInfo.authors).length > 1) {
        displayBook.appendChild('We recommend "' + title + '," by ' + author);
    }
    else {
        displayBook.appendChild('We recommend "' + title + '," by ' + author + " et al.");
    }

    //make a list of elements:
    displayBook.appendChild(createListElement('Description: ' + description));
    displayBook.appendChild(createListElement('Link: ' + linkify(link)));
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
  }

function randNum(num) {
    return Math.floor(Math.random() * num);
}

function linkify(inputText) {
    var replacedText, replacePattern;

    //URLs starting with http://, https://, or ftp://
    replacePattern = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=|!:,.;]*[-A-Z0-9+&@#\/%=|])/gim;
    replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');
    return replacedText;
}