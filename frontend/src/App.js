import { LinkContainer } from 'react-router-bootstrap';
import React, { Component, Fragment } from 'react';
import { Link, withRouter } from 'react-router-dom';

import { Nav, Navbar, NavItem } from 'react-bootstrap';
import Routes from './Routes';
import './App.css';


class App extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isAuthenticated: false,
			isAuthenticating: true,
			currentUser: null,
			userProfile: null,
		};
	}

	async checkIfLoggedIn() {
		if (JSON.parse(localStorage.getItem('currentUser')) !== null)
			return true
		else return false
	}

	async logOut() {
		localStorage.removeItem('currentUser');
		this.setState({ isAuthenticated: false })
	}

	async componentDidMount() {
		try {
			if (await this.checkIfLoggedIn()) {
				this.state.currentUser = JSON.parse(localStorage.getItem('currentUser'))
				this.userHasAuthenticated(true);
			}
		} catch (e) {
			if (e !== 'No current user') {
				alert(e);
			}
		}

		this.setState({ isAuthenticating: false });
	}



	userHasAuthenticated = authenticated => {
		this.setState({ isAuthenticated: authenticated });
	};

	setCurrentUser = user => {
		this.setState({ currentUser: user });
	};

	setUserProfile = profile => {
		this.setState({ userProfile: profile})
	}

	handleLogout = async event => {
		await this.logOut();

		this.userHasAuthenticated(false);
		this.props.history.push('/login');
	};
	render() {
		const childProps = {
			isAuthenticated: this.state.isAuthenticated,
			userHasAuthenticated: this.userHasAuthenticated,
			currentUser: this.state.currentUser,
			setCurrentUser: this.setCurrentUser,
			setUserProfile: this.setUserProfile
		};
		return (
				<div className="App container">
					<Navbar fluid collapseOnSelect>
						<Navbar.Header>
							<Navbar.Brand>
								<Link to="/">HR Recruitment App</Link>
							</Navbar.Brand>
							</Navbar.Header>
							<Nav pullLeft>
							{
								this.state.isAuthenticated && (
									
									<Fragment>
										{ localStorage.getItem('profile') == "Candidate" ? (
											<Navbar.Brand>
												<Link to="/my_tests">Moje testy</Link>
											</Navbar.Brand>
										):(
											<Fragment>
													<LinkContainer to="/my_tests">
														<NavItem>
															Moje testy
														</NavItem>
													</LinkContainer>
													<LinkContainer to="/tests"><NavItem>Testy</NavItem></LinkContainer>										
													<LinkContainer to="/manageCandidates"><NavItem>Zarządzaj kandydatami</NavItem></LinkContainer>								
											</Fragment>
										)
									}
									</Fragment>
								)
							}
							</Nav>
							<Navbar.Toggle />
						<Navbar.Collapse>
							<Nav pullRight>
								{this.state.isAuthenticated ? (
									<Fragment>
										<NavItem>{localStorage.getItem('currentUsername')} - {localStorage.getItem('profile')}</NavItem>
										<NavItem onClick={this.handleLogout}>Logout</NavItem>
									</Fragment>
								) : (
									<Fragment>
										<LinkContainer to="/signup">
											<NavItem>Zarejestruj się</NavItem>
										</LinkContainer>
										<LinkContainer to="/login">
											<NavItem>Zaloguj się</NavItem>
										</LinkContainer>
									</Fragment>
								)}
							</Nav>
						</Navbar.Collapse>
					</Navbar>
					<Routes childProps={childProps} profile={localStorage.getItem('profile')} isAuthenticated={this.state.isAuthenticated}/>
				</div>
		);
	}
}

export default withRouter(App);
