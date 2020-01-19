import React, { Component } from 'react';
import LoaderButton from '../components/LoaderButton';
import Axios from 'axios';
import { Link } from "react-router-dom";
import {
    HelpBlock,
    FormGroup,
    Glyphicon,
    FormControl,
    ControlLabel
  } from "react-bootstrap";
import './ForcePasswordChange.css'

export default class ForcePasswordChange extends Component {
	constructor(props) {
		super(props);

		this.state = {
            password: '',
            confirmPassword: '',
            resetSuccessful: false,
            isSendingRequest: false
		};
    }

	validateForm() {
        return this.state.password.length > 0 &&
        this.state.password === this.state.confirmPassword;
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
			Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/forcePasswordChange',
			{
                ["email"]: this.props.location.state.email,
                ["password"]: this.state.password,
                ["token"]: this.props.location.state.responseData.session
			}).then(res => {
				this.setState({ passwordSuccessful: true });
			}).catch(res => {
			})
		} catch (e) {
			alert(e.message);
			this.setState({ isSendingRequest: false });
		}
    };
    
    renderPasswordForms() {
        return (
            <form onSubmit={this.handleSubmit}>
                <h1>Set your password</h1>
                <FormGroup bsSize="large" controlId="password">
                    <ControlLabel>Password</ControlLabel>
                    <FormControl
                        autoFocus
                        type="password"
                        value={this.state.password}
                        onChange={this.handleChange}
                    />
                </FormGroup>
                <FormGroup bsSize="large" controlId="confirmPassword">
                    <ControlLabel>Confirm Password</ControlLabel>
                    <FormControl
                        autoFocus
                        type="password"
                        value={this.state.confirmPassword}
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
            <p>You have set your password successfully</p>
            <p>
              <Link to="/login">
                Click here to login with your new credentials.
              </Link>
            </p>
          </div>
        );
      }

	render() {
        return (
          <div className="ForceResetPassword">
            {!this.state.passwordSuccessful ? this.renderPasswordForms() : this.renderSuccessMessage()}
          </div>
        );
      }
}
