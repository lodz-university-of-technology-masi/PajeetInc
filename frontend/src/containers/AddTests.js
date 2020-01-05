import React, {useState, useReducer} from 'react'
import { PageHeader, HelpBlock, FormGroup, FormControl, ControlLabel, ListGroup, ListGroupItem, Panel, Checkbox} from 'react-bootstrap';
import {Button} from 'react-bootstrap'
import TestAdded from '../components/TestAdded'
import Axios from 'axios';
import CSVReader from 'react-csv-reader'

export default function AddTests({history}) {
  const [questionType, setQuestionType] = useState("O")
  const [questionText, setQuestionText] = useState("")
  const [questionAnswere, setQuestionAnswere] = useState(null)
  const [answersNumber, setAnswersNumber] = useState([0])
  const [correctAnswere, setCorrectAnswere] = useState(false)
  const [answeresClosed, setAnsweresClosed] = useState([])
  const [testName, setTestName] = useState("")
  const [minPoints, setMinPoints] = useState("")
  const [maxPoints, setMaxPoints] = useState("")

  function reducer(state, action) {
    switch (action.type) {
      case 'addQuestion':
        return [...state, action.payload];
      default:
        throw state;
    }
  }

  const handleForce = data => {
    data.forEach((q) => {
      removeEmpty(q)
      console.log(q)
      if(q.type == 'L'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        setQuestionAnswere(q.correctAnswer)
        dispatch({type: "addQuestion", payload: {content:q.content, type: q.type, correctAnswer: q.correctAnswer} })
      } 
      if(q.type == 'O'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        dispatch({type: "addQuestion", payload: {content:q.content, type: q.type, correctAnswer: questionAnswere} })
      } 
      if(q.type == 'W'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        const answeres = []
        for(let i = 0; i < Object.keys(q).length - 5; i++) {
          answeres.push({answer: q[`answers/${i}/answer`], correct:q[`answers/${i}/correct`]});
        }
        dispatch({type: "addQuestion", payload: {content:q.content, type: q.type, answers:answeres }});      
      } 
    })
  };

  const submitTest = () => {
    console.log({["recruiter-id"]:"rekruter420",["test-name"]:testName, ["min-points"]: minPoints,["max-points"]: maxPoints ,questions})
    Axios.post('https://dxix4h5we1.execute-api.us-east-1.amazonaws.com/dev/tests',{["recruiter-id"]:"rekruter420",["test-name"]:testName, ["min-points"]: minPoints,["max-points"]: maxPoints ,questions})
        .then(() => {
          history.push('/tests')
        })
  
  }
  const removeEmpty = obj => {
    Object.keys(obj).forEach(key => obj[key] == null && delete obj[key]);
  };

  const [questions, dispatch] = useReducer(reducer, [])
  return (

    

    <div>
      <PageHeader>Dodawanie testu</PageHeader>
      <CSVReader onFileLoaded={handleForce}
                 label="Importuj plik csv"  
                 parserOptions={{header: true,
                 dynamicTyping: true,
                 skipEmptyLines: true}}
      />
      <ControlLabel>Nazwa testu</ControlLabel>
      <FormControl as="textarea" rows="3" onChange={(e) => setTestName(e.target.value)} />
      <ControlLabel>Minimalna Liczba Punktów</ControlLabel>
      <FormControl as="textarea" rows="3" onChange={(e) => setMinPoints(e.target.value)} />
      <ControlLabel>Maksymalna Liczba Punktów</ControlLabel>
      <FormControl as="textarea" rows="3" onChange={(e) => setMaxPoints(e.target.value)} />
      <p>Typ Pytania</p>
      <FormControl componentClass="select" onChange={(e) => setQuestionType(e.target.value)}>
        <option value="O">Otwarte</option>
        <option value="L">Liczbowe</option>
        <option value="W">Zamknięte</option>
      </FormControl>
      {questionType == "O" ? (
      <FormGroup controlId="exampleForm.ControlTextarea1">
        <ControlLabel>Pytanie</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
      </FormGroup>
      ) : null
      }
      {questionType == "L" ? (
      <FormGroup controlId="exampleForm.ControlTextarea1">
        <ControlLabel>Pytanie</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
        <ControlLabel>Odpowiedz</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setQuestionAnswere(e.target.value)} />
      </FormGroup>
      ) : null
      }
      {questionType == "W" ? (
      <FormGroup controlId="exampleForm.ControlTextarea1">
        <ControlLabel>Pytanie</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
        <Panel>
          {answersNumber.map((num) => {
            return(
              <ListGroup>
              <ControlLabel>Odpowiedź {num}</ControlLabel>
              <FormControl as="textarea" rows="3" onChange={(e) => setQuestionAnswere(e.target.value)} />
              <Checkbox onClick={() => setCorrectAnswere(true)}>
                Poprawna Odpowiedz?
              </Checkbox>
              <Button onClick={() =>{setAnswersNumber([...answersNumber, num + 1]); setAnsweresClosed([{answer: questionAnswere, correct:correctAnswere},...answeresClosed])}}>Dodaj Odpowiedz</Button>
            </ListGroup> 
            )
          })
        }
        <Button onClick={() =>{
           setAnsweresClosed([{answer: questionAnswere, correct:correctAnswere},...answeresClosed]);
          }}>Zatwierdź Odpowiedzi</Button>
        </Panel>
        </FormGroup>
      ) : null
      }
      {    
        questionType == "W" ? (
      <div>
        <Button variant="primary" onClick={() => {setAnsweresClosed([{answere: questionAnswere, correct:correctAnswere},...answeresClosed]); setAnswersNumber([0]); setCorrectAnswere(false); dispatch({type: "addQuestion", payload: {content:questionText, type: questionType, answers: answeresClosed} }); setAnsweresClosed([])}}>Dodaj Pytanie</Button>
      </div>
      )
      :
        <Button variant="primary" onClick={() => dispatch({type: "addQuestion", payload: {content:questionText, type: questionType, correctAnswer: questionAnswere} })}>Dodaj Pytanie</Button>
        }
      <TestAdded questions={questions} index={0}/>
      <Button type="submit" onClick={() => submitTest()}>Zatwierdź test</Button>
    </div>
  )
}
