import React from "react";
import { IntlProvider } from "react-intl";
import transations from './translations';



const Context = React.createContext();

class IntlProviderWrapper extends React.Component {
  constructor(...args) {
    super(...args);

    this.switchToEnglish = () =>
      this.setState({ locale: "en", messages: transations['en-GB'] });

    this.switchToPolish = () =>
      this.setState({ locale: "pl", messages: transations['pl-PL'] });

    // pass everything in state to avoid creating object inside render method (like explained in the documentation)
    this.state = {
      locale: "en",
      messages: transations['en-GB'],
      switchToEnglish: this.switchToEnglish, 
      switchToPolish: this.switchToPolish 
    };
  }

  render() {
    const { children } = this.props;
    const { locale, messages } = this.state;
    return (
      <Context.Provider value={this.state}>
        <IntlProvider
          key={locale}
          locale={locale}
          messages={messages}
          defaultLocale="en"
        >
          {children}
        </IntlProvider>
      </Context.Provider>
    );
  }
}

export { IntlProviderWrapper, Context as IntlContext };