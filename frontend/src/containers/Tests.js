import React, {useEffect, useState} from 'react'
import { PageHeader } from 'react-bootstrap'
import axios from 'axios';
import { LinkContainer } from 'react-router-bootstrap'
import {Button} from 'react-bootstrap'
import Test from '../components/Test'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'
import { CSVLink } from "react-csv";

export default function Tests() {
  const [tests, setTests] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/tests/'+ localStorage.getItem('currentUsername'),
      );
      console.log(result)
      setTests(result.data);
    };
    fetchData();
  }, []);

  
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


  return (
    
    <div>
     <PageHeader>Zarządzaj testami</PageHeader>
     <LinkContainer to="/add_tests">
      <Button variant="link">Dodaj test</Button>
     </LinkContainer>
     <h2>Testy</h2>
     <ListGroup>
      {tests.map((test, i) => {
        return ( 
        <div>
          {console.log(test)}
          <Test testName={test.testName} key={test.test_id} questions={test.questions}/>
          <CSVLink filename={`${test.testName}.csv`} data={handleForce(test.questions)}>Pobierz</CSVLink>
        </div>
        )
        })}
     </ListGroup>

    </div>
  )
}
