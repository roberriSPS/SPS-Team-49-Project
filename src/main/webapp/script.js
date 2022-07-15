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

    //generate a random number to examine:
    const rand = await randNum(bookList.length);
    const match = 0;

    while (match == 0) {
    if (booksList[rand].volumeInfo.language === "en") {
      const title =  bookList[rand].volumeInfo.title;
      const author = bookList[rand].volumeInfo.authors[0];
      const description = bookList[rand].volumeInfo.description;
      const link = bookList[rand].volumeInfo.previewLink;
      match = 1;
    }
    else {
        rand = await randNum(bookList.length);
    }
    }   

    //make a list of elements:
    displayBook.appendChild(createListElement('Primary Author: ' + author));
    displayBook.appendChild(createListElement('Title: ' + title));
    displayBook.appendChild(createListElement('Description: ' + description));
    displayBook.appendChild(createListElement('Link: ' + link));
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
  }

  function randNum(num) {
    return Math.floor(Math.random() * num);
}