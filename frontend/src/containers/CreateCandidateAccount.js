import React, { Component } from 'react';
import LoaderButton from '../components/LoaderButton';
import Axios from 'axios';
import {
    FormGroup,
    Glyphicon,
    FormControl,
    ControlLabel
  } from "react-bootstrap";
import './ForcePasswordChange.css'

export default class CreateCandidateAccount extends Component {
	constructor(props) {
		super(props);

		this.state = {
            email: '',
            creationSuccessful: false,
            isSendingRequest: false
		};
    }

	validateForm() {
        return this.state.email.length > 0;
	}

	handleChange = event => {
		this.setState({
			[event.target.id]: event.target.value
		});
	};

	handleSubmit = async event => {
		event.preventDefault();

		this.setState({ isSendingRequest: true });


		try {
			Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/createCandidateAccount',
			{
                ["email"]: this.state.email
			}).then(res => {
                console.log('Account created!');
                console.log(res)
				this.setState({ creationSuccessful: true });
			}).catch(res => {
				console.log(res)
			})
		} catch (e) {
			alert(e.message);
			this.setState({ isSendingRequest: false });
		}
    };
    
    renderEmailForm() {
        return (
            <form onSubmit={this.handleSubmit}>
                <FormGroup bsSize="large" controlId="email">
                    <ControlLabel>Email</ControlLabel>
                    <FormControl
                        autoFocus
                        type="email"
                        value={this.state.email}
                        onChange={this.handleChange}
                    />
                </FormGroup>
                <LoaderButton
                    block
                    type="submit"
                    bsSize="large"
                    loadingText="Sending…"
                    text="Confirm"
                    isLoading={this.state.isSendingRequest}
                    disabled={!this.validateForm()}
                />
            </form>
        );
    }

    renderSuccessMessage() {
        return (
          <div className="success">
            <Glyphicon glyph="ok" />
            <p>You have successfully created {this.state.email} candidate account.</p>
          </div>
        );
      }

	render() {
        return (
          <div className="ForceResetPassword">
            {!this.state.creationSuccessful ? this.renderEmailForm() : this.renderSuccessMessage()}
          </div>
        );
      }
}
