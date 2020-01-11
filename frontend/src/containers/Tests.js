import React, {useEffect, useState} from 'react'
import { PageHeader } from 'react-bootstrap'
import axios from 'axios';
import { LinkContainer } from 'react-router-bootstrap'
import {Button} from 'react-bootstrap'
import Test from '../components/Test'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'

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

  return (
    
    <div>
     <PageHeader>Zarządzaj testami</PageHeader>
     <LinkContainer to="/add_tests">
      <Button variant="link">Dodaj test</Button>
     </LinkContainer>
     <h2>Testy</h2>
     <ListGroup>
      {tests.map((test, i) => {
        return <Test testName={test.test_name} key={test.test_id} questions={test.questions}/>
      })}
     </ListGroup>

    </div>
  )
}
