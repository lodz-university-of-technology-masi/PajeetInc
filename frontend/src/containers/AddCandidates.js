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
          if(test.test_id == value){
              setTestName(test.test_name);
          }
      });
  }
  function submitCandidate(){
    axios.post('https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/assign-candidate',{["recruiter-id"]:localStorage.getItem('currentUsername'),["test-id"]:testId, ["test-name"]: testName,["username"]: username})
  }
  return (
    <div>
        <PageHeader>Przypisanie kandydatów do testu</PageHeader>
        <ControlLabel>Wybierz test</ControlLabel>
        <FormControl componentClass="select" onChange={(e) => setTestsValue(e.target.value)}>
            {tests.map((test, i) => {
                return <option value={test.test_id}>{test.test_name}</option>
            })}
        </FormControl>

        <ControlLabel>Wybierz Kandydata</ControlLabel>
        <FormControl componentClass="select" onChange={(e) => setUsername(e.target.value)}>
            {users.map((user, i) => {
                return <option value={user.attributes[3].value}>{user.attributes[3].value}</option>
            })}
        </FormControl>
        <Button type="submit" onClick={() => submitCandidate()}>Dodaj kandydata</Button>

    </div>
  )
}