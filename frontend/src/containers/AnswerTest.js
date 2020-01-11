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
      question: q.question_content,
      type: q.question_type,
      content: "",
      correct: false,
      rated: true
    } 
  }
  ))

  const submitTest = (e) => {
    e.preventDefault()
    axios.put('https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/pass-test',{answers,["recruiter-id"]: test.recruiter_id, ["test-id"]: test.test_id, ["testName"]: test.test_name, username: localStorage.getItem('currentUsername')}).then(() => {

      setshowAlertSucces(true);
    }).catch(() => {
      setshowAlertError(true)
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
              {question.question_type != "W" ? (
                <FormGroup >
                  <ControlLabel>{question.question_content}</ControlLabel>
                  <FormControl onChange={(e) =>{let nanswers = [...answers]; let currentanswer = nanswers[index]; currentanswer.content=e.target.value; setanswers(nanswers)}}/>
                </FormGroup>
              ): (
                <FormGroup >
                  <ControlLabel>{question.question_content}</ControlLabel>
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
