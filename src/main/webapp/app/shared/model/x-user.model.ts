import { IUser } from 'app/shared/model/user.model';
import { IVideoList } from 'app/shared/model/video-list.model';

export interface IXUser {
  id?: number;
  videoListUrlSlug?: string | null;
  internalUser?: IUser | null;
  videoLists?: IVideoList[] | null;
}

export const defaultValue: Readonly<IXUser> = {};
