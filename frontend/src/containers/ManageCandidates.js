import React, {useEffect, useState} from 'react'
import axios from 'axios';
import { LinkContainer } from 'react-router-bootstrap'
import {Panel, ListGroup, ListGroupItem, Button, PageHeader} from 'react-bootstrap'

export default function Candidates({history}) {
  const [candidates, setCandidates] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/listCandidates'
      );
      setCandidates(result.data);
    };
    fetchData();
  }, []);

  const deleteCandidate = email => {
    axios.delete(`https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/deleteCandidateAccount/${email}`).then(() => {
      window.location.reload()
    })
  }

  return (
    <div class="container">
      <PageHeader>Zarządzaj kandydatami</PageHeader>
    	<LinkContainer to="/add_candidates"><Button bsStyle="link"><h4>Dodaj kandydata do testu</h4></Button></LinkContainer>							
			<LinkContainer to="/createCandidateAccount"><Button bsStyle="link"><h4>Stwórz konto kandydata</h4></Button></LinkContainer>						
     <h2>Lista Kandydatów</h2>  
     <ListGroup>
        {candidates.map((candidate, i) => {
            return (
              <ListGroupItem style={{padding: "15px", display:"flex", justifyContent:"space-between", alignItems:"center"}} key={i}>
                 <React.Fragment>
                  <span>{candidate.attributes[candidate.attributes.length - 1].value}</span>
                  <Button onClick={() => deleteCandidate(candidate.attributes[candidate.attributes.length - 1].value)} style={{float:"right"}} bsStyle="danger">X</Button>
                </React.Fragment>
               </ListGroupItem>
            )
        })}
     </ListGroup>
    </div>
  )
}
