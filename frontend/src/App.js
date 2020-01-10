import { LinkContainer } from 'react-router-bootstrap';
import React, { Component, Fragment } from 'react';
import { Link, withRouter } from 'react-router-dom';

import { Nav, Navbar, NavItem } from 'react-bootstrap';
import Routes from './Routes';
import { IntlProviderWrapper } from './translations/IntProviderWrapper'
import { FormattedMessage } from 'react-intl';
import { LanguageSwitch } from './components/LanguageSwitch'
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
			<IntlProviderWrapper>
				<div className="App container">
					<Navbar fluid collapseOnSelect>
						<Navbar.Header>
							<Navbar.Brand>
								<Link to="/">Test application</Link>
							</Navbar.Brand>
							{
								this.state.isAuthenticated && (
									
									<div>
										{this.state.profile == "Candidate" ? (
											<div>
											<Navbar.Brand>
												<Link to="/my_tests">Moje testy</Link>
											</Navbar.Brand>
											</div>
										):(
											<div>
												<Navbar.Brand>
													<Link to="/">Test application</Link>
												</Navbar.Brand>
												<Navbar.Brand>
													<Link to="/my_tests">Moje testy</Link>
												</Navbar.Brand>
												<Navbar.Brand>
													<Link to="/tests">Testy</Link>
												</Navbar.Brand>
												<Navbar.Brand>
													<Link to="/add_tests">Dodaj test</Link>
												</Navbar.Brand>
												<Navbar.Brand>
													<Link to="/add_candidates">Dodaj kandydata</Link>
												</Navbar.Brand>
											</div>
										)
									}
									</div>
								)
							}
							<Navbar.Toggle />
						</Navbar.Header>
						<Navbar.Collapse>
							<Nav>
							<NavItem>
								<LanguageSwitch />
							</NavItem>
							</Nav>
							<Nav pullRight>
								{this.state.isAuthenticated ? (
									<NavItem onClick={this.handleLogout}>Logout</NavItem>
								) : (
									<Fragment>
										<LinkContainer to="/signup">
											<NavItem><FormattedMessage id="user.signUp"/></NavItem>
										</LinkContainer>
										<LinkContainer to="/login">
											<NavItem><FormattedMessage id="user.signIn"/></NavItem>
										</LinkContainer>
									</Fragment>
								)}
							</Nav>
						</Navbar.Collapse>
					</Navbar>
					<Routes childProps={childProps} profile={localStorage.getItem('profile')} isAuthenticated={this.state.isAuthenticated}/>
				</div>
			</IntlProviderWrapper>
		
		);
	}
}

export default withRouter(App);
