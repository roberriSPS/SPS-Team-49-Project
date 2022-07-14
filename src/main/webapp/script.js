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

async function displayBook() {
    const responseFromServer = await fetch('/sentiment');
    const books = await responseFromServer.json();
    const displayBook = document.getElementById('display-book');
    displayBook.innerHTML = '';

    //make a list of all book options
    const bookList = books.items;
        const title =  bookList[2].volumeInfo.title;
        const author = bookList[2].volumeInfo.authors[0];
        const description = bookList[2].volumeInfo.description;
        const link = bookList[2].volumeInfo.previewLink;

    //make a list of elements:
    displayBook.appendChild(createListElement('Author: ' + author));
    displayBook.appendChild(createListElement('Title: ' + title));
    displayBook.appendChild(createListElement('Description: ' + description));
    displayBook.appendChild(createListElement('Link: ' + link));
    
    //console.log(bookList[2]);
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
  }