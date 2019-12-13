import React, {useContext} from 'react';
import { IntlContext } from "../translations/IntProviderWrapper";
import { ButtonGroup, Button } from 'react-bootstrap'

export const LanguageSwitch = () => {
  const { switchToEnglish, switchToPolish, locale } = useContext(IntlContext);
  return (
    <ButtonGroup>
      <Button onClick={switchToEnglish} active={locale == "en"}>English</Button>
      <Button onClick={switchToPolish} active={locale == "pl"}>Polish</Button>
    </ButtonGroup>
  );
};