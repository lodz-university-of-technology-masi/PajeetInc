import React, {useEffect, useState} from 'react'
import axios from 'axios';
import {Checkbox,ControlLabel,FormControl, FormGroup, Button, Panel, ListGroup, ListGroupItem} from 'react-bootstrap'

export default function AnswerTest({test}) {
  const [tests, setTests] = useState({});
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/tests/maciej@wp.ru',
      );
      setTests(result.data);
      console.log(result.data)
    };
    fetchData();
  }, []);
  return (
    <form>
      <Panel>
        <Panel.Heading>
          <Panel.Title>{test.test_name}</Panel.Title>
        </Panel.Heading>
        <Panel.Body>
        {test.questions.map((question)=>{
          return(
            <div>
            {question.question_type != "W" ? (
              <FormGroup >
                <ControlLabel>{question.question_content}</ControlLabel>
                <FormControl/>
              </FormGroup>
            ): (
              <FormGroup >
                <ControlLabel>{question.question_content}</ControlLabel>
                <FormGroup>
                  {question.answers.map((answer)=>{
                    return(
                        <Checkbox>{answer.answer}</Checkbox>
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
        <Button type="submit">Submit</Button>
      </Panel.Footer>
     </Panel>
    </form>
  )
}
