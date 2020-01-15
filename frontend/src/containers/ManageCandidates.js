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


  return (
    
    <div class="container">
     <h2>Lista Kandydatów</h2>  
     <ListGroup>
        {candidates.map((candidate, i) => { //Czy mozesz tu wrzucic do buttona handler onClick ktory usuwa
                                            //podanego uzytkownika?
                                            //POST na powyzszy link  /dev/deleteCandidateAccount
                                            //tylko email w requescie {"email": "kpm14005@eveav.com"}
            return (
            <div>
                <ListGroupItem key={i}>{JSON.stringify(candidate.attributes[3].value).slice(1,-1)}</ListGroupItem>
                <Button variant="danger">X</Button>
            </div>
            )
        })}
     </ListGroup>

    </div>
  )
}
