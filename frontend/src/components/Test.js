import React from 'react'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'

export default function Test({questions, testName}) {
  return (
    
    <ListGroupItem header={testName} style={{marginBottom: "25px"}}>
      {questions.map((question)=>{
       return(
              <Panel>
                <Panel.Heading>{question.question_content}</Panel.Heading>
                  {question.question_type == "L" ? (<Panel.Body>{question.correct_answer}</Panel.Body>) : null} 
                  {question.question_type == "W" ? (
                <Panel.Body>
                  <ListGroup>
                    {question.answers.map((answer)=>{
                      return(
                        <div>
                        <ListGroupItem bsStyle={answer.correct ? "success" : "warning"}>{answer.answer}</ListGroupItem>
                        </div>
                        )
                    })}
                  </ListGroup> 
                </Panel.Body>) : null} 
              </Panel>
       )
      })}
    </ListGroupItem>
  )
}
