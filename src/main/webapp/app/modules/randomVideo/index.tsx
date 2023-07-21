import React, { useEffect } from 'react';
import { Col, Row } from 'reactstrap';
import { Translate, translate} from 'react-jhipster';

import { locales, languages } from 'app/config/translation';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getRandomVideo, reset } from './random-video-reducer';
import { useParams } from 'react-router-dom';

export const RandomVideoPage = () => {
  const dispatch = useAppDispatch();
  // get random video url
  const video = useAppSelector(state => state.randomvideo);
  let { slug } = useParams();

  useEffect(() => {
    dispatch(getRandomVideo(slug));
  }, [dispatch, slug]);

  const randomVideo = useAppSelector(state => state.randomVideo.entity);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          slug = {slug}
          <br/>
          random video = {randomVideo.url}
        </Col>
      </Row>
    </div>
  );
};

export default RandomVideoPage;
