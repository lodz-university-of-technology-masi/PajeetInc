import React, {useEffect, useState} from 'react'
import axios from 'axios';
import {Checkbox,ControlLabel,FormControl, FormGroup, Button, Panel, ListGroup, ListGroupItem, Alert} from 'react-bootstrap'

export default function AnswerTest({test}) {
  const [isShown, setisShown] = useState(false)
  const [showAlertSucces, setshowAlertSucces] = useState(false)
  const [showAlertError, setshowAlertError] = useState(false)
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

  const submitTest = (e) => {
    e.preventDefault()
    axios.put('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/pass-test',{answers, recruiterId: test.recruiterId, testId: test.testId, testName: test.testName, username: localStorage.getItem('currentUsername')}).then(() => {

      setshowAlertSucces(true);
    }).catch(() => {
      setshowAlertError(true)
    })
  
  }

  function translateText(text){
    console.log(test)
      const fetchData = async (text) => {
        const url = 'https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20200117T190129Z.8c8da91101b61874.291663a7f383e1fa699ed02a66e72274a5805970&lang=en&text=' + text;
        const result = await axios(
          url,
        );
        console.log(result.data.text[0])
      };
      fetchData(text)
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
      {console.log(answers)}
      {isShown && (
        <form>
          <Panel.Body>
          {test.questions.map((question, index)=>{
            return(
              <div>
              {question.type != "W" ? (
                <FormGroup >
          <ControlLabel>{question.content} {translatedTest[1]}</ControlLabel>
                  <FormControl onChange={(e) =>{let nanswers = [...answers]; let currentanswer = nanswers[index]; currentanswer.content=e.target.value; setanswers(nanswers)}}/>
                </FormGroup>
              ): (
                <FormGroup >
                  <ControlLabel>{question.content}</ControlLabel>
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
          <Button type="submit" onClick={(e) =>{submitTest(e)}}>Submit</Button>
        </Panel.Footer>
      </form>
      )}
    </div>
  )
}
