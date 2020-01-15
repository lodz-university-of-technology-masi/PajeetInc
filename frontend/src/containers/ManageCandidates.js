import React, {useEffect, useState} from 'react'
import { PageHeader } from 'react-bootstrap'
import axios from 'axios';
import { LinkContainer } from 'react-router-bootstrap'
import {Button} from 'react-bootstrap'
import {Panel, ListGroup, ListGroupItem} from 'react-bootstrap'

export default function Candidates() {
  const [candidates, setCandidates] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/listCandidates'
      );
      console.log(result)
      setCandidates(result.data);
      console.log(candidates)
    };
    fetchData();
  }, []);

  const deleteCandidate = email => {
    axios.post('https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/deleteCandidateAccount', { email: email })
  }

  return (
    <div class="container">
     <h2>Lista Kandydatów</h2>  
     <ListGroup>
        {candidates.map((candidate, i) => {
            return (
              <ListGroupItem style={{padding: "15px"}} key={i}>
                {console.log(candidate)}
                {candidate.attributes[3].value}
                <Button onClick={() => deleteCandidate(candidate.attributes[3].value)} style={{float:"right"}} bsStyle="danger">X</Button>
              </ListGroupItem>
            )
        })}
     </ListGroup>

    </div>
  )
}
