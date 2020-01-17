import React, {useState, useReducer} from 'react'
import { PageHeader, HelpBlock, FormGroup, FormControl, ControlLabel, ListGroup, ListGroupItem, Panel, Checkbox} from 'react-bootstrap';
import {Button} from 'react-bootstrap'
import TestAdded from '../components/TestAdded'
import Axios from 'axios';

export default function EditTest({history, oldquestions, location}) {
  console.log(location)
  const [questionType, setQuestionType] = useState("O")
  const [questionText, setQuestionText] = useState("")
  const [questionAnswere, setQuestionAnswere] = useState(null)
  const [answersNumber, setAnswersNumber] = useState([])
  const [answeresClosed, setAnsweresClosed] = useState([])
  const [testName, setTestName] = useState(location.state.testName)
  const [minPoints, setMinPoints] = useState(location.state.minPoints)
  const [points, setPoints] = useState(0)
  const [nameValid, setnameValid] = useState(null)
  const [minValid, setminValid] = useState(null)
  const [loading, setloading] = useState(false)

  function reducer(state, action) {
    switch (action.type) {
      case 'addQuestion':
        return [...state, action.payload];
      case 'removeQuestion':
        return state.filter((q) => q.content != action.payload)
      default:
        throw state;
    }
  }

  const editQuestion = (question) =>{
    dispatch({type:"removeQuestion", payload:question.content})
    if(question.type == "O") {
      setQuestionType("O")
      setQuestionText(question.content)
      setPoints(question.points)
    }
    if(question.type == "L") {
      setQuestionType("L")
      setQuestionText(question.content)
      setPoints(question.points)
      setQuestionAnswere(question.correct)
    }
    if(question.type == "W") {
      setQuestionType("W")
      setQuestionText(question.content)
      setPoints(question.points)
      setAnswersNumber([...Array(parseInt(question.answers.length)).keys()])
      setAnsweresClosed(question.answers)
    }
  }

  const submitTest = () => {
    setloading(true)
    if(testName === "") {
      setnameValid('error')
      if(minPoints === "") {
        setminValid('error')
      }
      return
    }
    if(minPoints === "") {
      setminValid('error')
      return
    }

    if(questions.length == 0) {
      return
    }
    let maxPoints = questions.reduce((prev, curr) => {
      console.log(prev, curr)
      return ( {points: parseFloat(prev.points) + parseFloat(curr.points) } )
    })
    console.log(maxPoints)
    Axios.put('https://owe6jjn5we.execute-api.us-east-1.amazonaws.com/dev/update-test',
              { recruiterId: localStorage.getItem('currentUsername'),
                testName:testName, 
                minPoints: String(minPoints),
                maxPoints: String(maxPoints.points),
                questions,
                testId: location.state.testId
              })
      .then(() => {
        setloading(false)
        history.push('/tests')
    })
  }

  const setupClosedAnswers = (e) => {
    if (isNaN(parseInt(e.target.value))) {
      setAnswersNumber([...Array(parseInt(0)).keys()])
    //  setAnsweresClosed([...Array(parseInt(0)).keys()].map((n) => {
    //    return  {answer:"", correct: false}
    //   }))
    } else {
      setAnswersNumber([...Array(parseInt(e.target.value)).keys()]);
      setAnsweresClosed([...Array(parseInt(e.target.value)).keys()].map((n) => {
        console.log(answeresClosed[n])
        return answeresClosed[n] ? {answer:answeresClosed[n].answer, correct: answeresClosed[n].correct} : {answer:"", correct: false}
       }))
    }
  }

  const [questions, dispatch] = useReducer(reducer, location.state.questions)
  return (
    <div>
      <PageHeader>Dodawanie testu</PageHeader>
      <FormGroup validationState={nameValid}>
        <ControlLabel>Nazwa testu</ControlLabel>
        <FormControl value={testName} as="textarea" rows="3" onChange={(e) => setTestName(e.target.value)} />
      </FormGroup>
      <FormGroup validationState={minValid}>
        <ControlLabel>Minimalna Liczba Punktów Do Zdania Testu</ControlLabel>
        <FormControl value={minPoints} as="textarea" rows="3" onChange={(e) => setMinPoints(e.target.value)} />
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
        <FormControl value={questionText} as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
      </FormGroup>
      ) : null
      }
      {questionType == "L" ? (
      <FormGroup controlId="exampleForm.ControlTextarea1">
        <ControlLabel>Pytanie</ControlLabel>
        <FormControl value={questionText} as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
        <ControlLabel>Odpowiedz</ControlLabel>
        <FormControl value={questionAnswere} as="textarea" rows="3" onChange={(e) => setQuestionAnswere(e.target.value)} />
      </FormGroup>
      ) : null
      }
      {questionType == "W" ? (
      <FormGroup controlId="exampleForm.ControlTextarea1">
        <ControlLabel>Pytanie</ControlLabel>
        <FormControl value={questionText} as="textarea" rows="3" onChange={(e) => setQuestionText(e.target.value)} />
        <ControlLabel>Liczba odpowidzi</ControlLabel>
        <FormControl value={answersNumber.length} as="textarea" rows="3" onChange={(e) => { setupClosedAnswers(e) } } />
        <Panel>
          {
          answersNumber.map((num) => {
            return(
              <ListGroup>
              <ControlLabel>Odpowiedź {num + 1}</ControlLabel>
              <FormControl value={answeresClosed[num].answer} as="textarea" rows="3" onChange={(e) => {  let nanswers = [...answeresClosed]; let currentanswer = answeresClosed[num]; currentanswer.answer=e.target.value; setAnsweresClosed(nanswers) } } />
              <Checkbox checked={answeresClosed[num].correct} onClick={(e) => { let nanswers = [...answeresClosed]; let currentanswer = answeresClosed[num]; currentanswer.correct=e.target.checked; setAnsweresClosed(nanswers) }}>
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
      <div>
        <Button variant="primary" onClick={() => { setAnswersNumber([]); setQuestionText(''); dispatch({type: "addQuestion", payload: {content:questionText, points: points , type: questionType, answers: answeresClosed} }); setAnsweresClosed([]) }}>Dodaj Pytanie</Button>
      </div>
      )
      :
        <Button variant="primary" onClick={() => {setQuestionText(); dispatch({type: "addQuestion", payload: {content:questionText, points: points , type: questionType, correct: questionAnswere } })}}>Dodaj Pytanie</Button>
      }
      <TestAdded onEdit={editQuestion} questions={questions} index={0}/>
      <Button disabled={loading} type="submit" onClick={() => submitTest()}>{loading ? 'Czekaj..' : 'Zatwierdź test'}</Button>
    </div>
  )
}
