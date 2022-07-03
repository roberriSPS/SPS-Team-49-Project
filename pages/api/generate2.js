import { Configuration, OpenAIApi } from "openai";

const configuration = new Configuration({
  apiKey: process.env.OPENAI_API_KEY,
});
const openai = new OpenAIApi(configuration);

export default async function (req, res) {
  const completion = await openai.createCompletion({
    model: "text-davinci-002",
    prompt: generateBook(req.body.bookInfo),
    temperature: 0.6,
  });
  res.status(200).json({ bookResponse: completion.data.choices[0].text });
}

function generateBook(feeling){
  const currentFeeling = feeling;

  return `Recommend me a book that makes me feel better 
  because I am feeling ${currentFeeling}, write its name and 
  author`;

}
