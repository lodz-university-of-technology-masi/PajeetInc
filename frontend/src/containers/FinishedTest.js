import React, {useEffect, useState} from 'react'
import { Button, Panel} from 'react-bootstrap'

export default function FinishedTest({test}) {
  const [isShown, setisShown] = useState(false)
 

  return (
    <div>
      <Button type="submit" onClick={() => {setisShown(!isShown)}}>{isShown ? "Ukryj Test":  "Pokaż Test" }</Button>
      {isShown && test.rated == true && (
        <form>
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
      </form>
      )}
      {isShown && test.rated == false && (
          <h5>Test nie został jeszcze oceniony</h5>
      )}
    </div>
  )
}
