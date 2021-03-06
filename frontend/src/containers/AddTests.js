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
  const [answersNumber, setAnswersNumber] = useState([])
  const [answeresClosed, setAnsweresClosed] = useState([])
  const [testName, setTestName] = useState("")
  const [minPoints, setMinPoints] = useState("")
  const [points, setPoints] = useState(0)
  const [nameValid, setnameValid] = useState(null)
  const [minValid, setminValid] = useState(null)
  const [loading, setloading] = useState(false)

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
      if(q.type == 'L'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        setQuestionAnswere(q.correct)
        setPoints(q.points)
        dispatch({type: "addQuestion", payload: {content:q.content, points: q.points, type: q.type, correct: q.correct} })
      } 
      if(q.type == 'O'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        setPoints(q.points)
        dispatch({type: "addQuestion", payload: {content:q.content, points: q.points, type: q.type, correct: questionAnswere} })
      } 
      if(q.type == 'W'){
        setQuestionType(q.type)
        setQuestionText(q.content)
        setPoints(q.points)
        const answeres = []
        for(let i = 0; i < Object.keys(q).filter((x) => x.includes("/answer")).length; i++) {
          answeres.push({answer: q[`answers/${i}/answer`], correct: Boolean(q[`answers/${i}/correct`])});
        }
        dispatch({type: "addQuestion", payload: {content:q.content, points: q.points, type: q.type, answers:answeres }});      
      } 
    })
  };

  const submitTest = () => {
    setloading(true)
    if(testName === "") {
      setnameValid('error')
      if(minPoints === "") {
        setminValid('error')
      }
      setloading(false)
      return
    }
    if(minPoints === "") {
      setminValid('error')
      setloading(false)
      return
    }

    if(questions.length == 0) {
      return
    }
    let maxPoints = questions.reduce((prev, curr) => {
      return ( {points: parseFloat(prev.points) + parseFloat(curr.points) } )
    })
    Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/tests',{recruiterId: localStorage.getItem('currentUsername'),testName:testName, minPoints: minPoints,maxPoints: String(maxPoints.points), questions})
      .then(() => {
        setloading(false)
        history.push('/tests')
    })
  }

  const removeEmpty = obj => {
    Object.keys(obj).forEach(key => obj[key] == null && delete obj[key]);
  };
  const setupClosedAnswers = (e) => {
    if (isNaN(parseInt(e.target.value))) {
      setAnswersNumber([...Array(parseInt(0)).keys()])
      setAnsweresClosed([...Array(parseInt(0)).keys()].map((n) => {
        return  {answer:"", correct: false}
       }))
    } else {
      setAnswersNumber([...Array(parseInt(e.target.value)).keys()]);
      setAnsweresClosed([...Array(parseInt(e.target.value)).keys()].map((n) => {
        return  {answer:"", correct: false}
       }))
    }
  }

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
      Przykładowy plik CSV importu
      <pre>
        <code>
        type,content,points,correct,answers/0/answer,answers/0/correct,answers/1/answer,answers/1/correct,answers/2/answer,answers/2/correct <br/>
        O,Opowiedz o przedmiocie,12,,,,,,, <br/>
        L,Jaka dostaniemy ocene,4,5,,,,,, <br/>
        W,Jak bardzo ten lubisz przedmiot,3,,wcale,true,bardzo bardzo ,true,bardzo,false <br/>
        </code>
      </pre>
      <FormGroup validationState={nameValid}>
        <ControlLabel>Nazwa testu</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setTestName(e.target.value)} />
      </FormGroup>
      <FormGroup validationState={minValid}>
        <ControlLabel>Minimalna Liczba Punktów Do Zdania Testu</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => setMinPoints(e.target.value)} />
      </FormGroup>
      <ControlLabel>Liczba Punktów Za Pytanie</ControlLabel>
      <FormControl as="textarea" rows="3" onChange={(e) => setPoints(e.target.value)} />
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
        <ControlLabel>Liczba odpowidzi</ControlLabel>
        <FormControl as="textarea" rows="3" onChange={(e) => { setupClosedAnswers(e) } } />
        <Panel>
          {
          answersNumber.map((num) => {
            return(
              <ListGroup>
              <ControlLabel>Odpowiedź {num + 1}</ControlLabel>
              <FormControl as="textarea" rows="3" onChange={(e) => {  let nanswers = [...answeresClosed]; let currentanswer = answeresClosed[num]; currentanswer.answer=e.target.value; setAnsweresClosed(nanswers) } } />
              <Checkbox onClick={(e) => { let nanswers = [...answeresClosed]; let currentanswer = answeresClosed[num]; currentanswer.correct=e.target.checked; setAnsweresClosed(nanswers) }}>
                Poprawna Odpowiedz?
              </Checkbox>
            </ListGroup> 
            )
          })
        }
        </Panel>
        </FormGroup>
      ) : null
      }
      {    
        questionType == "W" ? (
        <Button onClick={() => { setAnswersNumber([]); dispatch({type: "addQuestion", payload: {content:questionText, points: points , type: questionType, answers: answeresClosed} }); setAnsweresClosed([]) }}>Dodaj Pytanie</Button>
      )
      :
        <Button onClick={() => { dispatch({type: "addQuestion", payload: {content:questionText, points: points , type: questionType, correct: questionAnswere } })}}>Dodaj Pytanie</Button>
      }
      { questions.length > 0 && <TestAdded questions={questions} index={0}/> }
      <Button  bsStyle="primary" disabled={loading} type="submit" onClick={() => submitTest()}>{loading ? 'Czekaj..' : 'Zatwierdź test'}</Button>
    </div>
  )
}
