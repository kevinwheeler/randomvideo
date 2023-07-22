import React, { useEffect } from 'react';
import { Button, Col, Row } from 'reactstrap';
import { Translate, translate} from 'react-jhipster';
import { locales, languages } from 'app/config/translation';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { nextVideo, previousVideo, reset } from './random-video-reducer';
import { useParams, useLocation } from 'react-router-dom';
import './random-video.scss';

import { sanitizeUrl } from "@braintree/sanitize-url";

export const RandomVideoPage = () => {
  const dispatch = useAppDispatch();
  const randomVideo = useAppSelector(state => state.randomVideo.currentVideo);
  const hasPreviousVideo = useAppSelector(state => state.randomVideo.hasPreviousVideo);
  let { slug } = useParams();
  let location = useLocation();

  // if we're on the homepage, use a hardcoded slug
  if (location.pathname === "/") {
    slug = "homepage";
  }


  useEffect(() => {
    dispatch(reset())
    dispatch(nextVideo(slug));
  }, [dispatch, slug]);


  // Add autoplay and mute to query params if not present
  // Remove videoIdParam if passed as argument
  const prepareQueryParams = (parsedUrl: URL, videoIdParam?: string) => {
    const queryParams = parsedUrl.searchParams;
    
    if(videoIdParam) {
      queryParams.delete(videoIdParam);
    }
  
    if(!queryParams.has('autoplay')) {
      queryParams.set('autoplay', '1');
    }

    if(queryParams.get('autoplay') === '1' && !queryParams.has('mute')) {
      queryParams.set('mute', '1');
    }
  
    return queryParams.toString();
  }
  
  const getVideoIdAndQueryParams = (url: string) => {
    if (!url) {
      return null;
    }
    const sanitizedUrl = sanitizeUrl(url);
  
    // Prepend 'https://' if not present
    const urlWithProtocol = sanitizedUrl.startsWith('https://') 
      ? sanitizedUrl 
      : 'https://' + sanitizedUrl;
    
    const parsedUrl = new URL(urlWithProtocol);
  
    if(parsedUrl.hostname.endsWith('youtu.be')) {
      let videoId = parsedUrl.pathname.split('/')[1];
      const queryParamsString = prepareQueryParams(parsedUrl);
      return `${videoId}?${queryParamsString}`;
    } else if(parsedUrl.hostname.endsWith('youtube.com')) {
      const videoId = parsedUrl.searchParams.get('v');
      const queryParamsString = prepareQueryParams(parsedUrl, 'v');
      return `${videoId}?${queryParamsString}`;
    } else {
      return null;
    }
  }

  
  const sanitizeString = (string: string): string => {
    const entityMap = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;',
      '/': '&#x2F;',
      '`': '&#x60;',
      '=': '&#x3D;'
    };

    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
      return entityMap[s];
    });
  }

  function dispatchPreviousVideo(): void {
    dispatch(previousVideo());
  }

  function dispatchNextVideo(): void {
    dispatch(nextVideo(slug));
  }

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1>{sanitizeString(randomVideo.name)}</h1>
          <div className="video-responsive video-margin">
            <iframe
              width="853"
              height="480"
              src={`https://www.youtube.com/embed/${getVideoIdAndQueryParams(randomVideo.url)}`}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              title="Embedded youtube"
            />
          </div>
          <div className="video-controls">
              <Button onClick={dispatchPreviousVideo} disabled={!hasPreviousVideo}>Previous Video</Button>
              <Button onClick={dispatchNextVideo}>Next Video</Button>
          </div>

          {/* A call to action letting people know they can create their own video lists */}
          <div className="text-center">
            <p>
              {/* <Translate contentKey="randomVideo.createVideoList">Create your own video list</Translate> */}
              Create your own video list here!
            </p>
            <p>
              <a className="btn btn-primary" href="/video-list/new">
                Create list
              </a>
            </p>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default RandomVideoPage;
