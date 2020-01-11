import React, {useEffect, useState} from 'react'
import { PageHeader, HelpBlock, FormGroup, FormControl, ControlLabel, ListGroup, ListGroupItem, Panel, Checkbox} from 'react-bootstrap';
import axios from 'axios';
import { LinkContainer } from 'react-router-bootstrap'
import {Button} from 'react-bootstrap'
import Test from '../components/Test'
import ReactDOM from 'react-dom'

export default function AddCandidates() {
  const [testId, setTestId] = useState([]);
  const [testName, setTestName] = useState([]);
  const [username, setUsername] = useState([]);
  const [tests, setTests] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/tests/' + localStorage.getItem('currentUsername'),
      );
      console.log(result);
      setTests(result.data);
    };
    fetchData();
  }, []);

  const [users, setUsers] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/listCandidates',
      );

      console.log(result);
      setUsers(result.data);
    };
    fetchData();
  }, []);

  function setTestsValue(value){
      console.log(value)
      setTestId(value);
      tests.forEach(test => {
          if(test.testId == value){
              setTestName(test.testName);
          }
      });
  }
  function submitCandidate(){
    axios.put('https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/assign-candidate',{recruiterId:localStorage.getItem('currentUsername'),testId:testId, testName: testName,["username"]: username})
  }
  return (
    <div>
        <PageHeader>Przypisanie kandydatów do testu</PageHeader>
        <ControlLabel>Wybierz test</ControlLabel>
        <FormControl componentClass="select" onChange={(e) => setTestsValue(e.target.value)}>
            <option>{'Wybierz test'}</option>
            {tests.map((test, i) => {
                return <option value={test.testId}>{test.testName}</option>
            })}
        </FormControl>

        <ControlLabel>Wybierz Kandydata</ControlLabel>
        <FormControl componentClass="select" onChange={(e) => setUsername(e.target.value)}>
            <option>{'Wybierz kandydata'}</option>
            {users.map((user, i) => {
                return <option value={user.attributes[3].value}>{user.attributes[3].value}</option>
            })}
        </FormControl>
        <Button type="submit" onClick={() => submitCandidate()}>Dodaj kandydata</Button>

    </div>
  )
}