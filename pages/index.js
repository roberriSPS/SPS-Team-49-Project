import Head from "next/head";
import { useState } from "react";
import styles from "./index.module.css";

export default function Home() {
  const [phraseInput, setPhraseInput] = useState("");
  const [result, setResult] = useState();
  const [book, setBook] = useState();
  const [description, setDescription] = useState();

  async function onSubmit(event) {
    event.preventDefault();
    const response = await fetch("/api/generate", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ phrase: phraseInput }),
    });
    const data = await response.json();
    setResult(data.result);

    const response2 = await fetch("/api/generate2", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({bookInfo: result}),
    });

    const bookResult = await response2.json();
    setBook(bookResult.bookResponse);

    const response3 = await fetch("/api/description", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({bookSummary: book}),
    });

    const bookInformation = await response3.json();
    setDescription(bookInformation.bookDescription);
  
  }

  return (
    <div>
      <Head>
        <title>Team 49 project</title>
      </Head>

      <main className={styles.main}>
        <h3>Write a sentence that better describes how did you feel today, this week/month/year</h3>
        
        <form onSubmit={onSubmit}>
          <input
            type="text"
            name="phrase"
            placeholder="Write right here"
            value={phraseInput}
            onChange={(e) => setPhraseInput(e.target.value)}
          />
          <input type="submit" value="Recommend me a book" />
        </form>

        <div className= {styles.result}> You are feeling: {result}, we recommend you this book:</div>
        <div className={styles.result}>{book}</div>
        <p>{description}</p>
        
      </main>
    </div>
    
  );
}
