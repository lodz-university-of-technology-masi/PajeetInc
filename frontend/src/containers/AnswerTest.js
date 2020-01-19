import React, {useEffect, useState} from 'react'
import axios from 'axios';
import {Checkbox,ControlLabel,FormControl, FormGroup, Button, Panel, ListGroup, ListGroupItem, Alert} from 'react-bootstrap'

export default function AnswerTest({test}) {
  const [isShown, setisShown] = useState(false)
  const [showAlertSucces, setshowAlertSucces] = useState(false)
  const [showAlertError, setshowAlertError] = useState(false)
  const [translatedText, setTranslatedText] = useState("")
  const [loading, setloading] = useState(false)

  const [answers, setanswers] = useState(test.questions.map((q) => 
  { 
    return {
      question: q.content,
      type: q.type,
      content: "",
      correct: false,
      rated: true
    } 
  }
  ))
const [translatedTest, setTranslatedTest] = useState([])

  useEffect(() => {
    const fetchData = async () => {
      const contentsArray = test.questions.map((q) => q.content).join(',');
      const url = 'https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20200117T190129Z.8c8da91101b61874.291663a7f383e1fa699ed02a66e72274a5805970&lang=en&text="' + contentsArray;
      const result = await axios(
        url,
      )
      setTranslatedTest(result.data.text)
    };
    fetchData()
  }, [])


  const submitTest = (e) => {
    e.preventDefault()
    setloading(true);
    axios.put('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/pass-test',{answers, recruiterId: test.recruiterId, testId: test.testId, testName: test.testName, username: localStorage.getItem('currentUsername')}).then(() => {

      setshowAlertSucces(true);
      setloading(false);
    }).catch(() => {
      setshowAlertError(true)
      setloading(false);
    })
  
  }


  return (
    <div>
      { showAlertError && ( <Alert bsStyle="danger" onDismiss={() => { setshowAlertError(false); setisShown(false) } }>
        <h4>Error Occured</h4>
      </Alert> )
      }
      {showAlertSucces && ( <Alert bsStyle="success" onDismiss={() => { setshowAlertSucces(false); setisShown(false) }}>
        <h4>Test Submited</h4>
      </Alert> )
      }
      <Button type="submit" onClick={() => {setisShown(!isShown)}}>{isShown ? "Ukryj Test":  "Rozwiąż Test" }</Button>
      {isShown && (
        <form>
          <Panel.Body>
          {test.questions.map((question, index)=>{
            return(
              <div>
              {question.type != "W" ? (
                <FormGroup >
          <ControlLabel>{question.content} </ControlLabel>
                  <FormControl onChange={(e) =>{let nanswers = [...answers]; let currentanswer = nanswers[index]; currentanswer.content=e.target.value; setanswers(nanswers)}}/>
                </FormGroup>
              ): (
                <FormGroup >
                  <ControlLabel>{question.content}  </ControlLabel>
                  <FormGroup>
                    {question.answers.map((answer)=>{
                      return(
                          <Checkbox onClick={(e) => { let nanswers = [...answers]; let currentanswer = nanswers[index]; currentanswer.content=answer.answer; setanswers(nanswers)}}>{answer.answer}</Checkbox>
                        )
                    })}
                  </FormGroup>
                </FormGroup>
              )}
              </div>
            )
            })}
          </Panel.Body>
        <Panel.Footer>
          <Button disabled={loading} bsStyle="primary" type="submit" onClick={(e) =>{submitTest(e)}}>{loading ? 'Czekaj' : 'Zatwierdź'}</Button>
        </Panel.Footer>
      </form>
      )}
    </div>
  )
}
