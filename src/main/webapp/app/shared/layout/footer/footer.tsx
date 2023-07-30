import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        
        <p className="footer">
          Hire Me! Portfolio + Resume: <a href="https://www.kevinwheeler.net">https://www.kevinwheeler.net</a>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
