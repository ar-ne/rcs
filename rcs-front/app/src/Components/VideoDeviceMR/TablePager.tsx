import * as React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import {ComponentProps} from '@material-ui/data-grid';
import {Pagination} from "@material-ui/lab";


const useStyles = makeStyles({
    root: {
        display: 'flex',
    },
});

export const TablePager = (props: ComponentProps) => {
    const {pagination, api} = props;
    const classes = useStyles();

    return (
        <Pagination
            className={classes.root}
            color="primary"
            page={pagination.page}
            count={pagination.pageCount}
            onChange={(event: any, value: number) => api.current.setPage(value)}
            variant="outlined" shape="rounded"
            showFirstButton showLastButton
        />
    );
}
