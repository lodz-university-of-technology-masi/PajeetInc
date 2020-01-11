import React from 'react'
import { Panel } from 'react-bootstrap'

export default function AssignedTest({test}) {
 
  return (
    <Panel>
      <Panel.Body> Candidate: {test.username} </Panel.Body>
    </Panel>
  )
}
