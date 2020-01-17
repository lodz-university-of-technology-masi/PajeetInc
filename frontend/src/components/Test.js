import React from 'react'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'
import { CSVLink } from "react-csv";
import { Button } from 'react-bootstrap';
import axios from 'axios'
import { Link } from "react-router-dom";

export default function Test({questions, testName, testId, history, minPoints}) {
    
  const handleForce = data => {
    const newCSV = []
    data.forEach((q) => {
      if(q.type == 'L'){
        newCSV.push({content:q.content, points: q.points, type: q.type, correct: String(q.correct)})
      } 
      if(q.type == 'O'){
        newCSV.push({content:q.content, points: q.points, type: q.type, correct: ""} )
      } 
      if(q.type == 'W'){
        const answeres = {};
        q.answers.forEach((a, i) => {
          answeres[`answers/${i}/answer`] = a.answer
          answeres[`answers/${i}/correct`] = a.correct
        })
        newCSV.push({content:q.content, points: q.points,correct: "", type: q.type, ...answeres });      
      } 
    })
    return newCSV
  };

  const deleteTest = testId => {
    axios.delete(`https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/remove-test?recruiterId=${localStorage.getItem('currentUsername')}&testId=${testId}`).then(() => {
      window.location.reload()
    })
  }

  return (
    <ListGroupItem header={testName} style={{marginBottom: "25px"}}>
      {questions.map((question)=>{
       return(
              <Panel>
                <Panel.Heading>{question.content}<span style={{float: "right"}}>Points: {question.points}</span></Panel.Heading>
                  {question.type == "L" ? (<Panel.Body>{question.correct}</Panel.Body>) : null} 
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
     <Button bsStyle="link"> <CSVLink filename={`${testName}.csv`} data={handleForce(questions)}>Pobierz CSV</CSVLink> </Button>
     <Button style={{float: "right"}} onClick={() => deleteTest(testId)} bsStyle="danger">X</Button>
     <Link to={{pathname:'edit', state:{ testName, questions, minPoints, testId} }}>Edit</Link>
    </ListGroupItem>
  )
}
