import React, {useState, useEffect} from 'react'
import axios from 'axios'
import {Button, Panel, PageHeader} from 'react-bootstrap'
import AnswerTest from './AnswerTest'
import FinishedTest from './FinishedTest'
import AssignedTest from './AssignedTest'
import RateTest from '././RateTest'
import PanelFooter from 'react-bootstrap/lib/PanelFooter'

export default function UserTests() {
  const [tests, setTests] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const url = 'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/get-tests?user='+ localStorage.getItem('currentUsername') + '&role=' + localStorage.getItem('profile').toLowerCase() +'&status=assigned' ;
      const result = await axios(
        url,
      );
      console.log(result.data)
      setTests(result.data);
    };
    fetchData();
  }, []);
  
  const [finishedTests, setFinishedTests] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const url = 'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/get-tests?user='+ localStorage.getItem('currentUsername') + '&role=' + localStorage.getItem('profile').toLowerCase() +'&status=finished' ;
      const result = await axios(
        url,
      );
      setFinishedTests(result.data);
    };
    fetchData();
  }, []);

  const [ratedTests, setRatedTests] = useState([]);
  useEffect(() => {
    console.log(localStorage.getItem('profile'))
    if(localStorage.getItem('profile') == "Recruiter"){
      console.log("Pobieram ocenione testy")
      const fetchData = async () => {
        const url = 'https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/get-tests?user='+ localStorage.getItem('currentUsername') + '&role=' + localStorage.getItem('profile').toLowerCase() +'&status=rated' ;
        const result = await axios(
          url,
        );
        setRatedTests(result.data);
      };
      fetchData();
    }
  }, []);

  return (
    <div>
    {
      localStorage.getItem('profile') == "Candidate" ? (
        <PageHeader>Moje testy do przejścia</PageHeader>
      ) : (
        <PageHeader>Przydzielone testy</PageHeader>
      )
    }
      {tests.map((test, i)=>{
        return(
        <div> 
          <Panel>
            <Panel.Heading>
              <Panel.Title>{test.testName}</Panel.Title>
            </Panel.Heading>
          {
            localStorage.getItem('profile') == "Candidate" ? (
              <AnswerTest test={test} />
            ) : (
              <AssignedTest test={test} />
            )
          }
          </Panel>
        </div>
        )
    })}
    {
      localStorage.getItem('profile') == "Candidate" ? (
        <PageHeader>Moje zakończone testy</PageHeader>
      ) : (
        <PageHeader>Zakończone testy</PageHeader>
      )
    }
      {finishedTests.map((test, i)=>{
        return(
        <div> 
          {
            localStorage.getItem('profile') == "Candidate" ? (
            <Panel>
              <Panel.Heading>
                <Panel.Title>{test.testName}</Panel.Title>
              </Panel.Heading>
              <FinishedTest test={test} />
            </Panel>
            ) : (
              test.rated == false && (
                <Panel>
                  <Panel.Heading>
                    <Panel.Title>{test.testName}</Panel.Title>
                  </Panel.Heading>
                  <RateTest test={test} />
                </Panel>
            )
          )
          }
        </div>
        )
    })}
    {
      localStorage.getItem('profile') == "Recruiter" && (
        <PageHeader>Ocenione Testy</PageHeader> 
      )
    }
    {ratedTests.map((test, i)=>{
        return(
        <div>
          <Panel>
            <Panel.Heading>
              <Panel.Title>{test.testName}</Panel.Title>
            </Panel.Heading>
            <FinishedTest test={test} />
          </Panel>
        </div>
        )
    })}
    </div>
  )
}
