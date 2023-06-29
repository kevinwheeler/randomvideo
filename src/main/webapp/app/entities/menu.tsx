import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/x-user">
        <Translate contentKey="global.menu.entities.xUser" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/video-list">
        <Translate contentKey="global.menu.entities.videoList" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/video">
        <Translate contentKey="global.menu.entities.video" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
