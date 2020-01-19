import React, {useEffect, useState} from 'react'
import { Button, Panel} from 'react-bootstrap'
import PanelFooter from 'react-bootstrap/lib/PanelFooter'

export default function FinishedTest({test}) {
  const [isShown, setisShown] = useState(false)
 

  return (
    <div>
      <Button type="submit" onClick={() => {setisShown(!isShown)}}>{isShown ? "Ukryj Test":  "Pokaż Test" }</Button>
      {isShown && test.rated == true && (
        <Panel>
        {test.answers.map((answer, index)=>{
            return(
                <div>
                    <Panel>
                    <Panel.Heading> {answer.question} </Panel.Heading>
                    <Panel.Body> {answer.content} </Panel.Body>
                    </Panel>
                </div>
            )
        })}
        <Panel bsStyle={test.passed ? "succes" : "danger"}>
          <Panel.Heading >Zaliczony: {test.passed == true ? "TAK" : "NIE"}</Panel.Heading >
          <Panel.Body >Punkty: {test.points}</Panel.Body >
        </Panel>

      </Panel>
      )}
      {isShown && test.rated == false && (
          <h5>Test nie został jeszcze oceniony</h5>
      )}
    </div>
  )
}
