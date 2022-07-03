import { Configuration, OpenAIApi } from "openai";

const configuration = new Configuration({
  apiKey: process.env.OPENAI_API_KEY,
});
const openai = new OpenAIApi(configuration);

export default async function (req, res) {
  const completion = await openai.createCompletion({
    model: "text-davinci-002",
    prompt: generateDescription(req.body.bookSummary),
    temperature: 0.6,
  });
  res.status(200).json({ bookDescription: completion.data.choices[0].text });
}

function generateDescription(bookName) {
  return `Write a description about this book: ${bookName}`;
}