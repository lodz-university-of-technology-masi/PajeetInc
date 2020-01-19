import React from 'react'
import { Panel } from 'react-bootstrap'

export default function AssignedTest({test}) {
 
  return (
      <Panel.Body> Candidate: {test.username} </Panel.Body>
  )
}
