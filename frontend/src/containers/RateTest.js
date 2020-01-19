import React, {useState} from 'react'
import axios from 'axios';
import { Panel, Button, ListGroup, ListGroupItem , FormControl, Alert} from 'react-bootstrap'

export default function RateTest({test}) {
  const [isShown, setisShown] = useState(false)
  const [showAlertSucces, setshowAlertSucces] = useState(false)
  const [showAlertError, setshowAlertError] = useState(false)
  const [loading, setloading] = useState(false)

  const [answers, setanswers] = useState(test.answers.map((q) => 
  { 
    return {
      question: q.question,
      type: q.type,
      content: q.content,
      correct: q.correct=="false" ? false : true,
      rated: true,
      points: q.points
    } 
  }
  ))
  
  const rateTest = (e) => {
    e.preventDefault()
    setloading(true)
    axios.put('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/rate-test',{answers, recruiterId: localStorage.getItem('currentUsername'), testId: test.testId, testName: test.testName, username: test.username}).then(() => {
      setshowAlertSucces(true);
      setloading(false)
    }).catch(() => {
      setshowAlertError(true)
      setloading(false)
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
    <Panel>
      <Panel.Body> Candidate: {test.username} </Panel.Body>
      <Button type="submit" onClick={() => {setisShown(!isShown)}}>{isShown ? "Ukryj Test":  "Pokaż Test" }</Button>
      {isShown && (
        <form>
        {test.answers.map((answer, index)=>{
            return(
              <div>
              { answer.type!="O" ? (
                <div>
                    <Panel>
                    <Panel.Heading> {answer.question} </Panel.Heading>
                    <Panel.Body> {answer.content} </Panel.Body>
                    </Panel>
                </div>
                ) : (
                  <div>
                    <Panel>
                    <Panel.Heading> {answer.question} </Panel.Heading>
                    <Panel.Body>
                      <ListGroup> 
                        <ListGroupItem> {answer.content} </ListGroupItem>
                        <ListGroupItem>
                          <FormControl placeholder="0" onChange={(e) =>{let nanswers = [...answers]; let currentanswer = nanswers[index]; currentanswer.points=e.target.value; currentanswer.correct = currentanswer.points == "0" ? false : true;setanswers(nanswers)}}/>
                        </ListGroupItem>
                      </ListGroup>
                    </Panel.Body>
                    </Panel>
                </div>
                )
              }
              </div>
            )
        })}
        <Panel.Footer>
          <Button disabled={loading} bsStyle="primary" type="submit" onClick={(e) =>{rateTest(e)}}>{loading ? 'Czekaj' : 'Zatwierdź'}</Button>
        </Panel.Footer>
      </form>
      )}
    </Panel>
    </div>
  )
}
