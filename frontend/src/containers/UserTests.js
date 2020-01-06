import React, {useState, useEffect} from 'react'
import axios from 'axios'
import {Button, Panel} from 'react-bootstrap'
import AnswerTest from './AnswerTest'

export default function UserTests() {
  const [tests, setTests] = useState([]);
  const [showForm, setshowForm] = useState([])
  useEffect(() => {
    const fetchData = async () => {
      const result = await axios(
        'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/tests/maciej@wp.ru',
      );
      setTests(result.data);
      console.log(tests)
    };
    fetchData();
  }, []);
  return (
    <div>
      {tests.map((test, i)=>{
        return(
        <div>
          <AnswerTest test={test} />
        </div>
        )
    })}

    </div>
  )
}
