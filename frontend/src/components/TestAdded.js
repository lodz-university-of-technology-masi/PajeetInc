import React from 'react'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'

export default function TestAdded({questions, testName}) {
  return (
    
    <ListGroupItem header={testName} style={{marginBottom: "25px"}}>
      {questions.map((question)=>{
       return(
              <Panel>
                  <Panel.Heading>{question.content}</Panel.Heading>
                    {question.type == "L" ? (<Panel.Body>{question.correctAnswer}</Panel.Body>) : null} 
                    {question.type == "W" ? (
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